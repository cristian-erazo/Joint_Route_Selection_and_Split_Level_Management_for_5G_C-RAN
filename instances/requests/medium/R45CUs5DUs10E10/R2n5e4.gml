graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 1
    prc 5
    ant 8
    prb 5
    x 110
    y 52
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 6
    prb 3
    x 55
    y 23
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 6
    x 100
    y 45
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 9
    prb 3
    x 35
    y 103
  ]
  edge [
    source 0
    target 3
    bandwith 384
    delay 229
  ]
  edge [
    source 0
    target 2
    bandwith 517
    delay 157
  ]
  edge [
    source 0
    target 1
    bandwith 641
    delay 107
  ]
  edge [
    source 0
    target 4
    bandwith 306
    delay 248
  ]
]
