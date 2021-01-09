graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 2
    prc 5
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 3
    prb 3
    x 45
    y 29
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 6
    prb 6
    x 15
    y 87
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 6
    prb 5
    x 105
    y 91
  ]
  edge [
    source 0
    target 5
    bandwith 764
    delay 169
  ]
  edge [
    source 1
    target 3
    bandwith 220
    delay 428
  ]
  edge [
    source 2
    target 4
    bandwith 463
    delay 265
  ]
]
