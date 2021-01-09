graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
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
    prc 4
    ant 4
    prb 4
    x 54
    y 110
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 6
    prb 5
    x 33
    y 44
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 10
    prb 5
    x 40
    y 87
  ]
  edge [
    source 0
    target 4
    bandwith 205
    delay 183
  ]
  edge [
    source 1
    target 3
    bandwith 670
    delay 256
  ]
  edge [
    source 2
    target 5
    bandwith 367
    delay 193
  ]
]
