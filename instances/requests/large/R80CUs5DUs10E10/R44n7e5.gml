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
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 5
    ant 6
    prb 2
    x 77
    y 18
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 8
    prb 3
    x 64
    y 107
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 5
    prb 5
    x 40
    y 66
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 8
    prb 5
    x 49
    y 100
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 8
    prb 4
    x 100
    y 96
  ]
  edge [
    source 0
    target 6
    bandwith 169
    delay 375
  ]
  edge [
    source 0
    target 3
    bandwith 481
    delay 433
  ]
  edge [
    source 1
    target 2
    bandwith 149
    delay 455
  ]
  edge [
    source 1
    target 5
    bandwith 994
    delay 100
  ]
  edge [
    source 1
    target 4
    bandwith 260
    delay 465
  ]
]
